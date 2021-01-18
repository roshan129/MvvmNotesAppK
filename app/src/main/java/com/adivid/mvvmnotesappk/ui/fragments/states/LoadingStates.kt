package com.adivid.mvvmnotesappk.ui.fragments.states

sealed class LoadingStates {

    class Loading(var isLoading: Boolean) : LoadingStates()
    class Error(var message: String): LoadingStates()

}