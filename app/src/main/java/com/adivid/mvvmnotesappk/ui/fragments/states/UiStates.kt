package com.adivid.mvvmnotesappk.ui.fragments.states

sealed class UiStates {

    class Loading(var isLoading: Boolean) : UiStates()
    object Error: UiStates()

}