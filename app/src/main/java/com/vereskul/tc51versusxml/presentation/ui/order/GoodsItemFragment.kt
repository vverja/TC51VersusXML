package com.vereskul.tc51versusxml.presentation.ui.order

import android.graphics.Bitmap
import android.graphics.Color.argb
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.EAN13Writer
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.FragmentGoodsItemBinding
import com.vereskul.tc51versusxml.domain.models.GoodsModel

class GoodsItemFragment : Fragment() {
    private var goodsModel: GoodsModel? = null

    private var _binding: FragmentGoodsItemBinding? = null
    private val binding:FragmentGoodsItemBinding
        get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                goodsModel = it.getParcelable(ARG_PARAM1, GoodsModel::class.java)
            }else{
                @Suppress("DEPRECATION")
                goodsModel = it.getParcelable(ARG_PARAM1)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoodsItemBinding.inflate(layoutInflater)
        binding.itemName.text = goodsModel?.name
        binding.itemQtyPlan.text = goodsModel?.qty.toString()
        binding.itemQtyFact.text = goodsModel?.qty.toString()
        binding.itemBarcode.text = goodsModel?.barcode

        val eaN13Writer = EAN13Writer()
        val bitMatrix = eaN13Writer.encode(goodsModel?.barcode, BarcodeFormat.EAN_13, 400, 200)
        val height = bitMatrix.height
        val width = bitMatrix.width
        val pixels = IntArray(height*width)
        for(y in 0 until height){
            val offset = y * width
            for(x in 0 until width)
                pixels[offset + x] = if(bitMatrix[x, y])
                    argb(255, 0, 0, 0)
                else
                    argb(255, 255, 255, 255)
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        binding.barcodeBitmap.setImageBitmap(bitmap)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        @JvmStatic
        fun newInstance(param1: GoodsModel) =
            GoodsItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}