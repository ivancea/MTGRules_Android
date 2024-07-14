package com.ivancea.MTGRules.services

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import com.ivancea.MTGRules.R
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PermissionsRequesterService @Inject constructor(
    @param:ActivityContext private val context: Context) {

    @Inject lateinit var storageService: StorageService

    fun requestNotifications() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || context !is Activity) {
            return
        }

        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            return
        }

        if (!context.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            if (storageService.notificationsPermissionRequested) {
                return
            }

            storageService.notificationsPermissionRequested = true
        }

        AlertDialog.Builder(context)
            .setTitle(R.string.alert_notification_permission_title)
            .setMessage(R.string.alert_notification_permission_message)
            .setPositiveButton(R.string.dialog_ok) { _, _ ->
                context.requestPermissions(
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
            .show()
    }
}