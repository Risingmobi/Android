package com.alex.tur.client.ui.comment

import android.arch.lifecycle.MutableLiveData
import com.alex.tur.base.BaseViewModel
import com.alex.tur.helper.SingleLiveEvent
import com.alex.tur.model.OrderDescription
import javax.inject.Inject

class CommentViewModel @Inject constructor(
        val comment: MutableLiveData<OrderDescription>
): BaseViewModel() {

    val pickImageClickHandler = SingleLiveEvent<Unit>()
    val takePhotoClickHandler = SingleLiveEvent<Unit>()
    val doneClickHandler = SingleLiveEvent<OrderDescription>()

    fun onImageSelected(image: String?) {
        val commentDesc = comment.value
        commentDesc?.picture = image
        comment.value = commentDesc
    }

    fun onPickImageClicked() {
        pickImageClickHandler.call()
    }

    fun onTakePhotoClicked() {
        takePhotoClickHandler.call()
    }

    fun onPhotoSelected(image: String?) {
        val commentDesc = comment.value
        commentDesc?.picture = image
        comment.value = commentDesc
    }

    fun onRemoveImageClicked() {
        val commentDesc = comment.value
        commentDesc?.picture = null
        comment.value = commentDesc
    }

    fun onDoneClicked() {
        doneClickHandler.value = comment.value
    }

    fun onTextCommentChanged(text: String) {
        comment.value?.brieflyDescription = text
    }
}