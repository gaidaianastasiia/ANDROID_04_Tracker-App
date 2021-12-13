package com.example.trackerapp.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.example.trackerapp.R
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class BaseFragment<
        VM : BaseViewModel,
        VMAF : ViewModelAssistedFactory<VM>,
        VB : ViewBinding
        > : DaggerFragment() {
    private var viewBinding: VB? = null
    protected val binding: VB
        get() = viewBinding ?: throw IllegalStateException("View binding is not initialized")

    @Inject
    protected lateinit var viewModelAssistedFactory: VMAF

    protected abstract val viewModelClass: KClass<VM>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = createViewBinding(inflater, container).also { viewBinding = it }.root

    protected fun showErrorMessage() {
        val message = getString(R.string.error_message)
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    protected abstract fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): VB

    protected val viewModel: VM by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory(this, null, viewModelFactory())
        ).get(viewModelClass.java)
    }

    protected open fun viewModelFactory(): (SavedStateHandle) -> ViewModel = { savedStateHandle ->
        viewModelAssistedFactory.let { assistedFactory ->
            if (assistedFactory is BaseViewModelAssistedFactory<*>) {
                assistedFactory.create(savedStateHandle)
            } else {
                throw IllegalStateException(
                    "viewModelFactory() should be overridden as viewModelAssistedFactory is not BaseViewModelAssistedFactory"
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }
}