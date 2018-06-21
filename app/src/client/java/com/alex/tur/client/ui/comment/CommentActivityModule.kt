package com.alex.tur.client.ui.comment

import android.arch.lifecycle.MutableLiveData
import com.alex.tur.client.ui.comment.CommentActivity.Companion.EXTRA_COMMENT
import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.model.OrderDescription
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
abstract class CommentActivityModule {

    @ContributesAndroidInjector()
    abstract fun commentTextFragment(): CommentTextFragment

    @ContributesAndroidInjector()
    abstract fun commentImageFragment(): CommentImageFragment

    @Module
    companion object {

        @Provides
        @ScopeActivity
        @JvmStatic
        internal fun comment(activity: CommentActivity): MutableLiveData<OrderDescription> {
            return MutableLiveData<OrderDescription>().apply {
                value = (activity.intent.getSerializableExtra(EXTRA_COMMENT) as OrderDescription?) ?: OrderDescription()
            }
        }

        @Provides
        @ScopeActivity
        @JvmStatic
        internal fun action(activity: CommentActivity): String {
            return activity.intent.action
        }
    }
}