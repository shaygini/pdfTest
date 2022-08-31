package com.example.testpdf

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.testpdf.databinding.FragmentFirstBinding
import com.example.testpdf.pdfranderer.PdfCreateThumbnails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        testPdf()
    }

    fun testPdf() {

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val file = File(requireContext().filesDir, "60MB_test_Original_PDF_from_Kirtas.pdf")
                val pdfCreateThumbnails = PdfCreateThumbnails()
                val thubnailBitmap = pdfCreateThumbnails.createTumnailSprite(file)
                withContext(Dispatchers.Main) {
                    binding.image.setImageBitmap(thubnailBitmap)
                }
            } catch (e: Exception) {
                Log.d("FirstFragment", "error: $e")
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}