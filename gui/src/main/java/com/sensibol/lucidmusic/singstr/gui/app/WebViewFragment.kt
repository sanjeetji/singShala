package com.sensibol.lucidmusic.singstr.gui.app

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.*
import android.webkit.WebSettings.LOAD_NO_CACHE
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentWebViewBinding
import timber.log.Timber

class WebViewFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_web_view
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentWebViewBinding::inflate
    override val binding get():FragmentWebViewBinding = super.binding as FragmentWebViewBinding

    private val args: WebViewFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onInitView() {
        binding.apply {
            pbLoadingProgress.visibility = VISIBLE

            tvLog.visibility = VISIBLE
            tvLog.text = getString(R.string.loading)
            ivCloseview.setOnClickListener{
                findNavController().popBackStack()
            }

            wvContainer.apply {
                visibility = GONE
                settings.apply {
                    javaScriptEnabled = true
                    allowFileAccess = true
                    cacheMode = LOAD_NO_CACHE
                    domStorageEnabled = true
                    clearCache(true)
                    clearFormData()
                    clearHistory()

                    CookieManager.getInstance().setAcceptCookie(true)
                    when {
                        Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT -> {
                            mixedContentMode = 0
                            setLayerType(View.LAYER_TYPE_HARDWARE, null)
                            CookieManager.getInstance().setAcceptThirdPartyCookies(wvContainer, true);
                        }
                        Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT -> {
                            setLayerType(View.LAYER_TYPE_HARDWARE, null)
                        }
                        else -> {
                            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                        }
                    }
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        Timber.d("webView Url: ${url.toString()}")
                        return if(url?.contains("singshala.page.link") == true){
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                            true
                        } else
                            false
                    }

                    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                        Timber.e("onReceivedError:  $error")
                        super.onReceivedError(view, request, error)
                        showError("Oops! Something went wrong.")
                    }

                    override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                        Timber.e("onReceivedHttpError:  $errorResponse")
                        super.onReceivedHttpError(view, request, errorResponse)
                        showError("Oops! Something went wrong.")
                    }
                }
                webChromeClient = object : WebChromeClient() {

                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        if (100 == newProgress) showWebView()
                    }
                }
                loadUrl(args.url)
            }
        }
    }

    private fun showError(message: String) {
        Timber.d("showError: $message")
        binding.apply {
            tvLog.visibility = VISIBLE
            tvLog.text = message
            pbLoadingProgress.visibility = GONE
            wvContainer.visibility = GONE
        }
    }

    private fun showWebView() {
        Timber.d("showWebView: ")
        binding.apply {
            tvLog.visibility = GONE
            pbLoadingProgress.visibility = GONE
            wvContainer.visibility = VISIBLE
        }
    }


    override fun onDestroyView() {
        Timber.d("onDestroyView: ")
        binding.wvContainer.destroy()
        super.onDestroyView()
    }
}