# Gainful Stock Calculation Rules

This document describes all stock-related calculation formulas and business logic in the Gainful project.

---

## 1. Data Models

### Transaction

| Field | Type | Description |
|-------|------|-------------|
| `assetId` | String | Asset identifier (unifiedCode) |
| `type` | TransactionType | BUY / SELL / DIVIDEND |
| `quantity` | Double | Number of shares |
| `price` | Double | Execution price |
| `amount` | Double | Total cash amount (includes fees for BUY) |
| `tradeDate` | Long | Trade date (epoch ms) |
| `fee` | Double | Computed property, not persisted |

**Fee formula** (`Transaction.kt:13-18`):

```
BUY:      fee = amount - price × quantity
SELL:     fee = price × quantity - amount
DIVIDEND: fee = 0.0
```

> For BUY, `amount` includes the commission. For SELL, `amount` excludes the commission.

### KLine

| Field | Description |
|-------|-------------|
| `date` | Date string (yyyy-MM-dd) |
| `close` | Closing price |
| `changeAmount` | Price change (today close − previous close) |
| `changePercent` | Price change percentage (%) |

---

## 2. Holdings Calculation

### 2.1 Basic Holdings (GetHoldingsUseCase)

Grouped by `assetId`, sorted by `timestamp`, processed chronologically:

```
BUY:      totalCost += amount;  quantity += quantity
SELL:     avgCost = totalCost / quantity;
          totalCost -= avgCost × sellQuantity;  quantity -= quantity
DIVIDEND: totalCost -= amount
```

Result: returns `Holding(quantity, averageCost = totalCost / quantity)` when `quantity > 0`.

### 2.2 Display Holdings (GetHoldingsDisplayUseCase)

Same cost-basis logic, additionally accumulates `totalBuys`, `totalSells`, `totalDividends`:

```
BUY:      totalBuys += amount;  totalCost += amount;  quantity += quantity
SELL:     totalSells += amount;  avgCost = totalCost / quantity;
          totalCost -= avgCost × quantity;  quantity -= quantity
DIVIDEND: totalDividends += amount;  totalCost -= amount
```

### 2.3 HoldingDisplay Computed Properties (`HoldingDisplay.kt:19-26`)

```
totalMarketValue = currentPrice × quantity
totalCost        = averageCost × quantity
totalGain        = -totalBuys + totalSells + totalDividends + totalMarketValue
```

### 2.4 Closed Positions (GetClosedPositionsUseCase)

Returned when `quantity == 0` AND `totalSells > 0`:

```
realizedGain        = -totalBuys + totalSells + totalDividends
realizedGainPercent = (realizedGain / totalBuys) × 100    // when totalBuys > 0
```

---

## 3. Daily PnL Calculation

### 3.1 Position Snapshots (ComputePnlUseCase)

Algorithm: `computeDailyPositions()` backfills from the first transaction date:

1. Sort all transactions by `tradeDate`
2. At each new date boundary, snapshot current holdings as that date's **start-of-day position**
3. Apply that day's transactions (BUY +quantity, SELL −quantity, DIVIDEND does not affect holdings)
4. For dates with no transactions between first trade date and today, carry forward previous position

Output: `Map<LocalDate, Map<String, Double>>` — date → (assetId → start-of-day share count)

### 3.2 Daily Total PnL (calculateDayPnl)

**Part 1 — Unrealized position PnL** (market movement):

```
for each asset where startOfDayQuantity > 0:
    heldQuantity = startOfDayQuantity - sameDaySellQuantity
    if heldQuantity > 0:
        totalPnl += kline.changeAmount × heldQuantity
```

**Part 2 — Realized trade PnL** (execution quality):

```
BUY:      totalPnl += (todayClose - execPrice) × quantity;  totalPnl -= fee
SELL:     totalPnl += (execPrice - yesterdayClose) × quantity;  totalPnl -= fee
DIVIDEND: totalPnl += amount
```

> `yesterdayClose` = the most recent KLine entry whose date is strictly before today.

---

## 4. Per-Stock PnL Detail (StockPnlDetail)

### 4.1 Stocks with Position (held at start of day)

```
heldQuantity  = startOfDayQuantity - sameDaySellQuantity
positionPnl   = kline.changeAmount × heldQuantity
tradePnl      = (kline.close - buyPrice) × buyQuantity          // BUY
               (sellPrice - yesterdayClose) × sellQuantity       // SELL
dailyPnl      = positionPnl + tradePnl
pnl           = positionPnl + tradePnl - fee
```

### 4.2 Trade-Only Stocks (no position, traded that day)

```
tradePnl = (kline.close - buyPrice) × quantity                  // BUY
           (sellPrice - yesterdayClose) × quantity               // SELL
           amount                                                // DIVIDEND
dailyPnl = tradePnl (BUY/SELL) or 0.0 (DIVIDEND)
pnl      = tradePnl - fee
```

### 4.3 Field Reference

| Field | Description |
|-------|-------------|
| `positionPnl` | Unrealized PnL (price change × held shares) |
| `positionQuantity` | Held shares (start-of-day − same-day sells) |
| `tradePnl` | Realized trade PnL (execution price vs reference price × quantity) |
| `fee` | Commission (BUY or SELL) |
| `buyFee` | BUY commission (non-zero only for BUY) |
| `sellFee` | SELL commission (non-zero only for SELL) |
| `dividend` | Dividend amount (non-zero only for DIVIDEND) |
| `dailyPnl` | Daily total = positionPnl + tradePnl |

---

## 5. Portfolio Aggregation (DashboardUiState)

```
totalMarketValue = SUM(holding.totalMarketValue)
totalCost        = SUM(holding.totalCost)
totalGain        = -totalBuys + totalSells + totalDividends + totalMarketValue
totalGainPercent = (totalGain / totalBuys) × 100              // when totalBuys > 0
```

**Daily gain display**:

```
totalDailyGain        = SUM(daily PnL values)
previousDayValue      = totalMarketValue - totalDailyGain
totalDailyGainPercent = (totalDailyGain / previousDayValue) × 100
```

---

## 6. Stock Code & Asset Matching

The project does not parse SH/SZ/HK prefixes locally. It relies entirely on structured fields returned by the East Money API.

| Field | Description | Example |
|-------|-------------|---------|
| `code` | Stock ticker | `600519` |
| `unifiedCode` | Unified identifier | `600519` |
| `innerCode` | Internal numeric ID | `46077278941243` |
| `quoteId` | secId for API calls | `1.600519` (SH), `0.000858` (SZ) |
| `mktNum` | Market number | `1`=SH, `0`=SZ |

**Asset matching key**: `asset.unifiedCode.ifBlank { asset.code }`

**Stock detail matching** (`GetStockDetailUseCase`): triple match — `code == stockCode || unifiedCode == stockCode || innerCode == stockCode`

---

## 7. Market Data Fetching & Caching

### 7.1 API Endpoints

| Endpoint | Purpose |
|----------|---------|
| `push2.eastmoney.com/api/qt/stock/get` | Single stock real-time quote |
| `push2.eastmoney.com/api/qt/ulist.np/get` | Batch quotes |
| `push2.eastmoney.com/api/qt/stock/trends/get` | Intraday trends |
| `push2his.eastmoney.com/api/qt/stock/kline/get` | Historical KLine |
| `searchapi.eastmoney.com/api/suggest/get` | Stock search |

### 7.2 Caching

- **Quote snapshots**: Room database table `quote_snapshots`
- **KLine data**: Room database table `kline_cache`, incremental updates
- **Polling**: `StockPriceFetchService` background polling, runs only during market hours

### 7.3 Field Priority

Single-quote fields (f43+) take precedence over batch fields (f2+), unified via `resolved*` computed properties:

```kotlin
val resolvedLatestPrice: Double get() = latestPriceSingle ?: latestPrice ?: 0.0
```

---

## 8. PnL Period Display

PnL data is aggregated into four periods (calendar/week/month/year):

| Period | Granularity | Description |
|--------|-------------|-------------|
| DAY | Daily | Calendar view, daily PnL per month |
| WEEK | Weekly | Weekly cumulative PnL per month |
| MONTH | Monthly | Monthly cumulative PnL per year |
| YEAR | Yearly | Annual cumulative PnL |

All periods: `totalPnl` = sum of cell `value`s.

---

## 9. Important Constraints

- **No multi-currency support**: All amounts are in a single implicit currency (CNY). No exchange rate conversion.
- **No lot size validation**: No enforcement of market-specific lot sizes (e.g., HK 100-share lots). Quantities are user-entered as-is.
- **Commission embedded in amount**: BUY `amount = price × quantity + fee`, SELL `amount = price × quantity − fee`.
- **Dividends do not affect share count**: DIVIDEND transactions adjust `totalCost` only, not `quantity`.
- **No adjusted prices**: Raw KLine closing prices are used directly (no forward/backward adjustment).
