package org.videolan.vlc.gui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.videolan.vlc.R
import org.videolan.vlc.gui.onboarding.GoogleSignInHelper.SignInResult

class OnboardingWelcomeFragment : OnboardingFragment() {
    private lateinit var titleView: TextView

    override fun getDefaultViewForTalkback() = titleView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.onboarding_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView = view.findViewById(R.id.welcome_title)
        view.findViewById<View>(R.id.google_button).setOnClickListener { startGoogleSignIn() }
        // Continue as Guest moves on through onboarding without an account.
        view.findViewById<View>(R.id.guest_button).setOnClickListener { onboardingFragmentListener.onNext() }
        // Facebook and Email sign-in are not implemented yet.
        listOf(R.id.facebook_button, R.id.email_button).forEach {
            view.findViewById<View>(it).isEnabled = false
        }
    }

    private fun startGoogleSignIn() {
        val clientId = getString(R.string.hmp_google_web_client_id)
        if (clientId.startsWith("REPLACE_WITH")) {
            Toast.makeText(requireContext(), R.string.hmp_google_not_configured, Toast.LENGTH_LONG).show()
            return
        }
        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = GoogleSignInHelper.signIn(requireActivity(), clientId)) {
                is SignInResult.Success -> {
                    val who = result.displayName ?: result.email ?: ""
                    Toast.makeText(requireContext(), getString(R.string.hmp_signed_in_as, who), Toast.LENGTH_SHORT).show()
                    onboardingFragmentListener.onNext()
                }
                is SignInResult.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.hmp_sign_in_failed, result.message), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        fun newInstance(): OnboardingWelcomeFragment {
            return OnboardingWelcomeFragment()
        }
    }
}
