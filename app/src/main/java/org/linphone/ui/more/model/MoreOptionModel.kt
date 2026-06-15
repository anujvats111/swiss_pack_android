package org.linphone.ui.more.model

import androidx.annotation.DrawableRes

data class MoreOptionModel(
    val id: Int,
    val title: String,
    @DrawableRes val iconResId: Int
)
