package com.example.pointofmaps.translate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pointofmaps.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslateFragment : Fragment() {

    private var englishKoreanTranslator: Translator? = null
    private var mTranslateBtn : Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_translate, container, false)
        mTranslateBtn = rootView.findViewById<Button>(R.id.button1)
        val mTranslateText = rootView.findViewById<EditText>(R.id.inputTranslateEditText)
        val mTranslatedText = rootView.findViewById<TextView>(R.id.translatedtextView)

        mTranslateBtn?.setOnClickListener {
            englishKoreanTranslator?.translate(mTranslateText.text.toString())?.addOnSuccessListener { translatedText ->
                mTranslatedText.text = translatedText
            }?.addOnFailureListener { exception ->

            }
        }

        return rootView
    }

    override fun onStart() {
        super.onStart()

        getTranslator()

        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        englishKoreanTranslator?.downloadModelIfNeeded(conditions)
            ?.addOnSuccessListener {
                mTranslateBtn?.isEnabled = true
                getTranslator()
            }
            ?.addOnFailureListener { exception ->
                // Model couldn’t be downloaded or other internal error.
                // ...
            }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        englishKoreanTranslator?.close()
    }

    private fun getTranslator() {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.KOREAN)
            .build()
        englishKoreanTranslator = Translation.getClient(options)
        lifecycle.addObserver(englishKoreanTranslator!!)
    }
}
