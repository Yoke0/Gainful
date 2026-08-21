package com.yoke.gainful.network.exception

/**
 * Thrown when the server rejects the refresh token (expired, revoked or invalid),
 * meaning the user session can no longer be renewed and re-authentication is required.
 */
class RefreshTokenExpiredException : Exception("Refresh token has expired or been revoked")
