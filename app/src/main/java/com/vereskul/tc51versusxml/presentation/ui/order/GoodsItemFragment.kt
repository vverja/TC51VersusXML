package com.vereskul.tc51versusxml.presentation.ui.order

import android.graphics.Bitmap
import android.graphics.Color.argb
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.EAN13Writer
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.FragmentGoodsItemBinding
import com.vereskul.tc51versusxml.domain.models.GoodsModel
import com.vereskul.tc51versusxml.domain.models.OrderStatus
import com.vereskul.tc51versusxml.presentation.ui.login.afterTextChanged

class GoodsItemFragment : Fragment() {
    private val viewModel: OrderViewModel by activityViewModels()
    private var goodsModel: GoodsModel? = null
    private var itemIndex = 0
    private var _binding: FragmentGoodsItemBinding? = null
    private var canMakeChanges = false

    private val binding:FragmentGoodsItemBinding
        get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                goodsModel = it.getParcelable(GOODS_MODEL, GoodsModel::class.java)
            }else{
                @Suppress("DEPRECATION")
                goodsModel = it.getParcelable(GOODS_MODEL)
            }
            itemIndex = it.getInt(ITEM_INDEX)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_goods_item,
            container,
            false
        )
        //_binding = FragmentGoodsItemBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.goodsModel = goodsModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    fun getCurrentBarcode():String?{
        return goodsModel?.barcode
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.order.observe(viewLifecycleOwner){
            binding.goodsModel = it.goods[itemIndex]
        }
        checkCanMakeChanges()
        setOnClickListeners()
        setObserver()
    }

    private fun setOnClickListeners() {

        binding.buttonMinus1.setOnClickListener {
            if (!checkCanMakeChanges()) return@setOnClickListener
            changeValueQtyFact(-1.0)
        }
        binding.buttonMinus5.setOnClickListener {
            if (!checkCanMakeChanges()) return@setOnClickListener
            changeValueQtyFact(-5.0)

        }
        binding.buttonMinus10.setOnClickListener {
            if (!checkCanMakeChanges()) return@setOnClickListener
            changeValueQtyFact(-10.0)
        }
        binding.buttonPlus1.setOnClickListener {
            if (!checkCanMakeChanges()) return@setOnClickListener
            changeValueQtyFact(1.0)
        }
        binding.buttonPlus5.setOnClickListener {
            if (!checkCanMakeChanges()) return@setOnClickListener
            changeValueQtyFact(5.0)
        }
        binding.buttonPlus10.setOnClickListener {
            if (!checkCanMakeChanges()) return@setOnClickListener
            changeValueQtyFact(10.0)
        }
        binding.sendBarcodeTo1c.setOnClickListener {
            if (!checkCanMakeChanges()) return@setOnClickListener
            viewModel.sendBarcodeTo1c(binding.itemBarcode.text.toString())
        }



        binding.itemQtyFact.afterTextChanged {
            try{
                val value = it.toDouble()
                if (value<0){
                    binding.itemQtyFact.error = getString(R.string.error_fact_below_zero)
                }
            }catch (e: NumberFormatException){
                binding.itemQtyFact.error = getString(R.string.error_fact_not_digit)
            }
        }
        binding.itemBarcode.afterTextChanged {
            if (viewModel.checkGoodsBarcode(it) && canMakeChanges){
                binding.sendBarcodeTo1c.isEnabled = true
            }
        }
    }

    private fun changeValueQtyFact(digit: Double) {
        val itemQtyFact = binding.itemQtyFact
        var value = itemQtyFact.text.toString().toDouble()
        value += digit
        if (value >= 0) {
            itemQtyFact.setText(value.toString())
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.error_fact_below_zero),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }


    private fun setObserver(){
        viewModel.order.observe(viewLifecycleOwner){ order ->
            canMakeChanges = when (order.orderState) {
                OrderStatus.NEW -> false
                OrderStatus.IN_WORK -> true
                OrderStatus.IN_STOCK -> false
                OrderStatus.CANCELED -> TODO()
                null -> throw RuntimeException("Статус заказа не определен")
            }

            checkCanMakeChanges()
        }
    }

    private fun checkCanMakeChanges(): Boolean{
        if (!canMakeChanges){
            Snackbar.make(
                binding.root,
                getString(R.string.error_work_begin),
                Snackbar.LENGTH_SHORT
            ).show()
            binding.sendBarcodeTo1c.isEnabled = false
            binding.itemBarcode.isEnabled = false
            binding.itemQtyFact.isEnabled = false
        }else{
            binding.sendBarcodeTo1c.isEnabled = true
            binding.itemBarcode.isEnabled = true
            binding.itemQtyFact.isEnabled = true
        }
        return canMakeChanges
    }

    private fun createBarcodeImage() {
        val eaN13Writer = EAN13Writer()
        val bitMatrix = eaN13Writer.encode(goodsModel?.barcode, BarcodeFormat.EAN_13, 400, 200)
        val height = bitMatrix.height
        val width = bitMatrix.width
        val pixels = IntArray(height * width)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width)
                pixels[offset + x] = if (bitMatrix[x, y])
                    argb(255, 0, 0, 0)
                else
                    argb(255, 255, 255, 255)
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val GOODS_MODEL = "goodsModel"
        private const val ITEM_INDEX = "ItemIndex"
        @JvmStatic
        fun newInstance(goods: GoodsModel, itemIndex: Int) =
            GoodsItemFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(GOODS_MODEL, goods)
                    putInt(ITEM_INDEX, itemIndex)
                }
            }
    }
}