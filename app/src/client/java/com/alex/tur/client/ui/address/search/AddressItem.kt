package com.alex.tur.client.ui.address.search

abstract class AddressItem(val description: String, val placeId: String)

class FoundAddressItem(description: String, placeId: String) : AddressItem(description, placeId)

class HomeAddressItem(description: String, placeId: String = "") : AddressItem(description, placeId)

class CurrentAddressItem(description: String, placeId: String = "") : AddressItem(description, placeId)

class OnMapAddressItem(description: String, placeId: String = "") : AddressItem(description, placeId)