/**
 * Group 3 Android Demo (CS3301, 02/2020)
 * @author 170006583
 */

package com.example.slidesappv2

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class AdHandler(private val mainActivity: MainActivity) {

    private val adMobTestRewardID = "ca-app-pub-3940256099942544/5224354917"
    private val moneyName = "coins"

    // start loading a new ad
    internal fun getNewAd() {
        MobileAds.initialize(mainActivity)
        val rewardAd = RewardedAd(mainActivity, adMobTestRewardID)
        rewardAd.loadAd(AdRequest.Builder().build(), object : RewardedAdLoadCallback() {

            // called when ad loads
            override fun onRewardedAdLoaded() {
                val rewardCallback = object : RewardedAdCallback() {

                    // called if user earned a reward (finished ad)
                    override fun onUserEarnedReward(p0: RewardItem) {
                        mainActivity.earnMoney(p0.amount)
                        mainActivity.showToast("You earned ${p0.amount} $moneyName!")
                    }
                }

                // ad loaded, so enable button
                mainActivity.enableAdButton(rewardAd, rewardCallback)
            }

            // called when an ad failed to load
            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                mainActivity.showToast("Ad failed to load: $errorCode")
            }
        })
    }

}