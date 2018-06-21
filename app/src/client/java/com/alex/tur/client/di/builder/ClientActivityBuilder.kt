package com.alex.tur.client.di.builder

import com.alex.tur.client.ui.login.AuthActivityModule
import com.alex.tur.client.ui.login.AuthActivity
import com.alex.tur.client.ui.main.MainActivity
import com.alex.tur.client.ui.main.MainActivityModule
import com.alex.tur.client.ui.profile.edit.ClientEditActivity
import com.alex.tur.client.ui.profile.edit.ClientEditActivityModule
import com.alex.tur.client.ui.splash.SplashActivity
import com.alex.tur.client.ui.splash.SplashActivityModule
import com.alex.tur.di.scope.ScopeActivity
import com.alex.tur.client.ui.address.map.AddressActivityModule
import com.alex.tur.client.ui.address.map.AddressMapActivity
import com.alex.tur.client.ui.address.search.AddressSearchActivity
import com.alex.tur.client.ui.address.search.AddressSearchActivityModule
import com.alex.tur.client.ui.comment.CommentActivity
import com.alex.tur.client.ui.comment.CommentActivityModule
import com.alex.tur.client.ui.companies.CompanyListActivity
import com.alex.tur.client.ui.companies.CompanyListActivityModule
import com.alex.tur.client.ui.history.HistoryActivity
import com.alex.tur.client.ui.history.HistoryActivityModule
import com.alex.tur.client.ui.services.ServicesListActivity
import com.alex.tur.client.ui.services.ServicesListActivityModule
import com.alex.tur.client.ui.template.TemplateTimeActivity
import com.alex.tur.client.ui.template.TemplateTimeActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ClientActivityBuilder {

    @ScopeActivity
    @ContributesAndroidInjector(modules = [AuthActivityModule::class])
    internal abstract fun bindLoginActivity(): AuthActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [SplashActivityModule::class])
    internal abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [AddressActivityModule::class])
    internal abstract fun bindAddressActivity(): AddressMapActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [ClientEditActivityModule::class])
    internal abstract fun bindEditActivity(): ClientEditActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [AddressSearchActivityModule::class])
    internal abstract fun bindAddressSearchActivity(): AddressSearchActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [ServicesListActivityModule::class])
    internal abstract fun bindServicesListActivity(): ServicesListActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [CompanyListActivityModule::class])
    internal abstract fun bindCompanyListActivity(): CompanyListActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [TemplateTimeActivityModule::class])
    internal abstract fun bindTemplateTimeActivity(): TemplateTimeActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [CommentActivityModule::class])
    internal abstract fun bindCommentActivity(): CommentActivity

    @ScopeActivity
    @ContributesAndroidInjector(modules = [HistoryActivityModule::class])
    internal abstract fun bindHistoryActivity(): HistoryActivity
}