package com.alex.tur.client.ui.address.search

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.alex.tur.R
import com.alex.tur.base.BaseActivity
import com.alex.tur.client.datamanager.addresssearch.PlacesManager
import com.alex.tur.client.ui.address.map.AddressMapActivity
import com.alex.tur.client.ui.address.map.AddressViewModel
import com.alex.tur.di.module.ui.ViewModelFactory
import com.alex.tur.ext.getViewModel
import com.alex.tur.helper.AddressFetcher
import com.alex.tur.helper.Result
import com.alex.tur.model.MyAddress
import com.alex.tur.recycleradapter.SimpleCompositeAdapter
import com.alex.tur.ui.profile.edit.QualifierAddressMapAction
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.client.activity_address_serach.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AddressSearchActivity : BaseActivity() {

    @Inject
    lateinit var placesManager: PlacesManager

    lateinit var adapter: SimpleCompositeAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AddressSearchViewModel>

    lateinit var viewModel: AddressSearchViewModel

    @Inject
    @JvmField
    @field:QualifierAddressMapAction
    var action: String? = null

    @Inject
    lateinit var addressFetcher: AddressFetcher

    private var textChangeDisposable: Disposable? = null

    private var homeAddressDisposable: Disposable? = null

    private var currentAddressDisposable: Disposable? = null

    private var homeAddressItem: HomeAddressItem? = null

    private var currentAddressItem: CurrentAddressItem? = null

    private var onMapAddressItem: OnMapAddressItem? = null

    companion object {

        const val EXTRA_HOME_ADDRESS = "EXTRA_HOME_ADDRESS"
        const val EXTRA_CURRENT_ADDRESS = "EXTRA_CURRENT_ADDRESS"
        const val EXTRA_SELECTED_ADDRESS = "EXTRA_SELECTED_ADDRESS"

        private const val RC_ADDRESS_MAP = 1

        fun startForResult(fragment: Fragment?, extras: Bundle?, requestCode: Int) {
            fragment?.startActivityForResult(Intent(fragment.context, AddressSearchActivity::class.java).apply {
                putExtras(extras)
            }, requestCode)
        }

        fun start(fragment: Fragment?, address: MyAddress?) {
            fragment?.startActivity(Intent(fragment.context, AddressSearchActivity::class.java).apply {
                putExtra(EXTRA_HOME_ADDRESS, address)
                action = AddressMapActivity.ACTION_CHANGE_HOME
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_serach)
        setSupportActionBar(toolbar)
        progressBar.visibility = View.INVISIBLE
        lifecycle.addObserver(placesManager)

        viewModel = getViewModel(AddressSearchViewModel::class, viewModelFactory)
        viewModel.addressAction.observe(this, Observer {
            it?.let {
                when(it.status) {
                    Result.Status.LOADING -> {
                        showLoading()
                    }
                    Result.Status.SUCCESS -> {
                        finish()
                    }
                    Result.Status.ERROR -> {
                        hideLoading()
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        adapter = SimpleCompositeAdapter.Builder()
                .add(FoundAddressDelegateAdapter().apply {
                    setOnItemClickListener { addressItem ->
                        placesManager.fetchPlaceDetail(addressItem.placeId, {
                            done(MyAddress.selected(it.latLng, it.address.toString()))
                        })
                    }
                })
                .add(CurrentAddressDelegateAdapter().apply {
                    setOnItemClickListener {
                        viewModel.addressCurrent.value?.also {
                            done(it)
                        }
                    }
                })
                .add(HomeAddressDelegateAdapter().apply {
                    setOnItemClickListener {
                        viewModel.addressHome.value?.also {
                            done(it)
                        }
                    }
                })
                .add(OnMapAddressDelegateAdapter().apply {
                    setOnItemClickListener {
                        if (action == AddressMapActivity.ACTION_CHANGE_HOME) {
                            AddressMapActivity.startForResult(this@AddressSearchActivity, viewModel.addressHome.value, AddressMapActivity.ACTION_CHANGE_HOME, RC_ADDRESS_MAP)
                        } else {
                            AddressMapActivity.startForResult(this@AddressSearchActivity, viewModel.addressHome.value, RC_ADDRESS_MAP)
                        }
                    }
                })
                .build()

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        initSelectAddressOnMapItem()
        initHomeAddressItem()
        initCurrentAddressItem()

        textChangeDisposable = RxTextView.afterTextChangeEvents(editText)
                .skipInitialValue()
                .map {
                    progressBar.visibility = View.VISIBLE
                    it
                }
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    placesManager.setQuery(it.editable().toString(), object: PlacesManager.Callback {
                        override fun loading(isLoading: Boolean) {
                            if (isLoading) {
                                progressBar.visibility = View.VISIBLE
                            } else {
                                progressBar.visibility = View.INVISIBLE
                            }
                        }
                        override fun result(result: MutableList<AddressItem>) {
                            adapter.swapData(result)
                            if (result.isEmpty()) {
                                adapter.addToStart(homeAddressItem)
                                adapter.addToStart(currentAddressItem)
                            }
                            adapter.add(onMapAddressItem)
                        }
                    })
                }, {
                    Timber.e(it, "text change error")
                })
    }

    private fun initSelectAddressOnMapItem() {
        onMapAddressItem = OnMapAddressItem("Enter location on map")
        adapter.add(onMapAddressItem)
    }

    private fun initHomeAddressItem() {
        viewModel.addressHome.observe(this, Observer { myAddress ->
            if (myAddress != null) {
                myAddress.addressString?.also {addressString ->
                    homeAddressItem = HomeAddressItem(addressString)
                    adapter.addToStart(homeAddressItem)
                }?: run {
                    homeAddressDisposable = addressFetcher.fetchAddressAsync(myAddress.latLng, myAddress.type)
                            .subscribe({
                                homeAddressItem = HomeAddressItem(it!!.addressString!!)
                                adapter.addToStart(homeAddressItem)
                            }, {
                                Timber.e(it, "address fetch error")
                            })
                }
            }
        })
    }

    private fun initCurrentAddressItem() {
        viewModel.addressCurrent.observe(this, Observer { myAddress ->
            if (myAddress != null) {
                myAddress.addressString?.also {
                    currentAddressItem = CurrentAddressItem(it)
                    adapter.addToStart(currentAddressItem)
                }?: run {
                    currentAddressDisposable = addressFetcher.fetchAddressAsync(myAddress.latLng, myAddress.type)
                            .subscribe({
                                currentAddressItem = CurrentAddressItem(it!!.addressString!!)
                                adapter.addToStart(currentAddressItem)
                            }, {
                                Timber.e(it, "address fetch error")
                            })
                }
            }
        })
    }


    private fun done(address: MyAddress) {
        if (action == AddressMapActivity.ACTION_CHANGE_HOME) {
            viewModel.changeAddress(address)
        } else {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(EXTRA_SELECTED_ADDRESS, address)
            })
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_ADDRESS_MAP && resultCode == Activity.RESULT_OK) {
            if (action == AddressMapActivity.ACTION_CHANGE_HOME) {
                finish()
            } else {
                data?.getParcelableExtra<MyAddress>(AddressMapActivity.EXTRA_MY_ADDRESS)?.also {
                    setResult(Activity.RESULT_OK, Intent().apply {
                        putExtra(EXTRA_SELECTED_ADDRESS, it)
                    })
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textChangeDisposable?.dispose()
        homeAddressDisposable?.dispose()
        currentAddressDisposable?.dispose()
    }
}