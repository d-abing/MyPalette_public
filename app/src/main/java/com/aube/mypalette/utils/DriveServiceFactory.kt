package com.aube.mypalette.utils

import android.content.Context

object DriveServiceFactory {
    fun create(context: Context, accountName: String): com.google.api.services.drive.Drive {
        val credential =
            com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
                .usingOAuth2(
                    context,
                    listOf(com.google.api.services.drive.DriveScopes.DRIVE_APPDATA)
                )
                .apply { selectedAccountName = accountName }

        return com.google.api.services.drive.Drive.Builder(
            com.google.api.client.http.javanet.NetHttpTransport(),
            com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("My Palette")
            .build()
    }
}