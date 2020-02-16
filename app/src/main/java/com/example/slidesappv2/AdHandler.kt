package com.example.slidesappv2

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdHandler(private val mainActivity: MainActivity) {

    private val adMobTestRewardID = "ca-app-pub-3940256099942544/5224354917"

    internal fun getNewAd() {
        MobileAds.initialize(mainActivity)
        val rewardAd = RewardedAd(mainActivity, adMobTestRewardID)
        rewardAd.loadAd(AdRequest.Builder().build(), object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {

                // enable button
                val rewardCallback = object: RewardedAdCallback() {
                    override fun onUserEarnedReward(p0: RewardItem) {
                        mainActivity.earnMoney(p0.amount)
                        mainActivity.showToast("You earned ${p0.amount}!")
                    }
                }

                mainActivity.activateAdButton(rewardAd, rewardCallback)
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                mainActivity.showToast("Ad failed to load: $errorCode")
            }
        })
    }


}