package ir.food.operatorAndroid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.ItemCartBinding
import ir.food.operatorAndroid.helper.StringHelper
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.ProductsModel

class CartAdapter(list: ArrayList<ProductsModel>) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private val models = list

    class ViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(MyApplication.context), parent, false
        )
        TypefaceUtil.overrideFonts(binding.root)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.binding.txtCount.text = StringHelper.toPersianDigits("${model.quantity}")
        holder.binding.txtName.text = model.name
        holder.binding.txtPrice.text = StringHelper.toPersianDigits(StringHelper.setComma(model.price))
    }

    override fun getItemCount(): Int {
        return models.size
    }
}