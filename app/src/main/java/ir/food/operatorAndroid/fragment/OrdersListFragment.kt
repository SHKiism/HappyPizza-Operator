package ir.food.operatorAndroid.fragment

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ir.food.operatorAndroid.R
import ir.food.operatorAndroid.adapter.OrdersAdapter
import ir.food.operatorAndroid.app.EndPoints
import ir.food.operatorAndroid.app.MyApplication
import ir.food.operatorAndroid.databinding.FragmentOrdersListBinding
import ir.food.operatorAndroid.dialog.SearchDialog
import ir.food.operatorAndroid.helper.TypefaceUtil
import ir.food.operatorAndroid.model.OrderModel
import ir.food.operatorAndroid.okHttp.RequestHelper
import org.json.JSONObject

class OrdersListFragment : Fragment() {

    lateinit var binding: FragmentOrdersListBinding
    var value = "family"

    var orderModels: ArrayList<OrderModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersListBinding.inflate(layoutInflater)

        TypefaceUtil.overrideFonts(binding.root)
        binding.edtSearchBar.requestFocus()

        binding.imgRefresh.setOnClickListener {
            if (binding.edtSearchBar.text.toString() == "") {
                MyApplication.Toast("لطفا موردی برای جست و جو وارد کنید", Toast.LENGTH_LONG)
                binding.edtSearchBar.requestFocus()
                return@setOnClickListener
            }
            getOrders(binding.edtSearchBar.text.toString())
        }

        binding.imgSearch.setOnClickListener {
            if (binding.edtSearchBar.text.toString() == "") {
                MyApplication.Toast("لطفا موردی برای جست و جو وارد کنید", Toast.LENGTH_LONG)
                binding.edtSearchBar.requestFocus()
                return@setOnClickListener
            }
            getOrders(binding.edtSearchBar.text.toString())
        }

        binding.imgSearchType.setOnClickListener {
            SearchDialog().show(object : SearchDialog.SearchListener {
                override fun searchType(searchType: Int) {
                    when (searchType) {
                        1 -> {
                            binding.imgSearchType.setImageResource(R.drawable.ic_user)
                            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_TEXT
                            value = "family"
                        }
                        2 -> {
                            binding.imgSearchType.setImageResource(R.drawable.ic_phone)
                            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_NUMBER
                            value = "mobile"
                        }
                        3 -> {
                            binding.imgSearchType.setImageResource(R.drawable.ic_gps)
                            binding.edtSearchBar.inputType = InputType.TYPE_CLASS_TEXT
                            value = "address"
                        }
                    }
                }
            })
        }

        binding.imgBack.setOnClickListener { MyApplication.currentActivity.onBackPressed() }

        return binding.root
    }

    private fun getOrders(searchText: String) {

        var model = OrderModel(
            "1",
            "pardakht",
            1,
            "23 tir",
            "fati noori",
            "09015693808",
            "mashhad"
        )

        orderModels.add(model)

        binding.vfOrders.displayedChild = 1
        RequestHelper.builder(EndPoints.GET_ORDERS)
            .listener(callBack)
            .addPath(value)
            .addPath(searchText)
            .get()
    }

    private val callBack: RequestHelper.Callback =
        object : RequestHelper.Callback() {
            override fun onResponse(reCall: Runnable?, vararg args: Any?) {
                MyApplication.handler.post {
                    try {
                        val jsonObject = JSONObject(args[0].toString())
                        val status = jsonObject.getBoolean("success")
                        val message = jsonObject.getString("message")
                        if (status) {
                            val dataArr = jsonObject.getJSONArray("data")
                            for (i in 0 until dataArr.length()) {
                                val dataObj = dataArr.getJSONObject(i)
                                var model = OrderModel(
                                    dataObj.getString("id"),
                                    dataObj.getJSONObject("status").getString("name"),
                                    dataObj.getJSONObject("status").getInt("code"),
                                    dataObj.getString("createdAt"),
                                    dataObj.getJSONObject("customer").getString("family"),
                                    dataObj.getJSONObject("customer").getString("mobile"),
                                    dataObj.getString("address")
                                )
                                orderModels.add(model)
                            }
                            if (orderModels.size == 0) {
                                binding.vfOrders.displayedChild = 0
                            } else {
                                binding.vfOrders.displayedChild = 2
                                val adapter = OrdersAdapter(orderModels)
                                binding.searchList.adapter = adapter
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        binding.vfOrders.displayedChild = 3
                    }
                }
            }

            override fun onFailure(reCall: Runnable?, e: java.lang.Exception?) {
                MyApplication.handler.post {
                    binding.vfOrders.displayedChild = 3
                }
            }
        }

}