package com.zhulin.guideshop.feature_topic.presentation.topics

import com.zhulin.guideshop.feature_topic.data.remote.responses.SubscriptionList


data class SubscriptionState(
    val subscriptions: SubscriptionList = SubscriptionList(),
)
