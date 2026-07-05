import WidgetKit
import SwiftUI

struct PnlEntry: TimelineEntry {
    let date: Date
    let title: String
    let noDataText: String
    let gainText: String
    let pctText: String
    let isPositive: Bool
    let hasData: Bool
}

struct PnlProvider: TimelineProvider {
    func placeholder(in context: Context) -> PnlEntry {
        PnlEntry(date: Date(), title: "今日盈亏", noDataText: "暂无数据", gainText: "+1,245.80", pctText: "+1.23%", isPositive: true, hasData: true)
    }

    func getSnapshot(in context: Context, completion: @escaping (PnlEntry) -> Void) {
        completion(loadEntry())
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<PnlEntry>) -> Void) {
        let entry = loadEntry()
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 15, to: Date())!
        completion(Timeline(entries: [entry], policy: .after(nextUpdate)))
    }

    private func loadEntry() -> PnlEntry {
        let d = UserDefaults(suiteName: "group.com.yoke.gainful")
        return PnlEntry(
            date: Date(),
            title: d?.string(forKey: "widget_title") ?? "Today's P&L",
            noDataText: d?.string(forKey: "widget_no_data") ?? "No data",
            gainText: d?.string(forKey: "widget_gain_text") ?? "",
            pctText: d?.string(forKey: "widget_pct_text") ?? "",
            isPositive: d?.bool(forKey: "widget_is_positive") ?? true,
            hasData: d?.bool(forKey: "widget_has_data") ?? false
        )
    }
}

struct PnlWidgetView: View {
    @Environment(\.widgetFamily) var family
    var entry: PnlProvider.Entry

    var body: some View {
        VStack(alignment: .center, spacing: 4) {
            Text(entry.title)
                .font(.system(size: family == .systemSmall ? 12 : 13, weight: .medium))
                .foregroundColor(Color(red: 0.54, green: 0.54, blue: 0.60))

            if !entry.hasData {
                Text(entry.noDataText)
                    .font(.system(size: family == .systemSmall ? 14 : 16))
                    .foregroundColor(Color(red: 0.54, green: 0.54, blue: 0.60))
            } else {
                Text(entry.gainText)
                    .font(.system(size: family == .systemSmall ? 28 : 36, weight: .heavy, design: .monospaced))
                    .foregroundColor(entry.isPositive
                        ? Color(red: 1.0, green: 0.843, blue: 0.0)
                        : Color(red: 0.906, green: 0.298, blue: 0.235))

                Text(entry.pctText)
                    .font(.system(size: family == .systemSmall ? 16 : 20, weight: .bold, design: .monospaced))
                    .foregroundColor(entry.isPositive
                        ? Color(red: 0.290, green: 0.855, blue: 0.502)
                        : Color(red: 0.906, green: 0.298, blue: 0.235))
            }
        }
        .padding(12)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
        .containerBackground(for: .widget) {
            Color(red: 0.027, green: 0.043, blue: 0.082)
        }
    }
}

struct PnlWidget: Widget {
    let kind: String = "TodayPnlWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: PnlProvider()) { entry in
            PnlWidgetView(entry: entry)
        }
        .configurationDisplayName("widget_title_key")
        .description("widget_desc_key")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}

@main
struct PnlWidgetBundle: WidgetBundle {
    var body: some Widget {
        PnlWidget()
    }
}
