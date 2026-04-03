package com.example.project

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var selectedMethod = "card"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.tvPaySubtotal.text = getString(R.string.product_price, CartManager.getSubtotal())
        binding.tvPayDelivery.text = getString(R.string.product_price, CartManager.getDeliveryFee())
        binding.tvPayTotal.text    = getString(R.string.product_price, CartManager.getTotal())

        selectMethod("card")

        binding.btnMethodCard.setOnClickListener { selectMethod("card") }
        binding.btnMethodCash.setOnClickListener { selectMethod("cash") }

        binding.btnPlaceOrder.setOnClickListener {
            if (selectedMethod == "card") {
                val name   = binding.etCardName.text.toString().trim()
                val number = binding.etCardNumber.text.toString().trim()
                val expiry = binding.etCardExpiry.text.toString().trim()
                val cvv    = binding.etCardCvv.text.toString().trim()

                if (name.isEmpty() || number.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                    Toast.makeText(this, "Please fill in all card details", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (number.length < 16) {
                    Toast.makeText(this, "Enter a valid 16-digit card number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                val name    = binding.etFullName.text.toString().trim()
                val phone   = binding.etPhone.text.toString().trim()
                val address = binding.etAddress.text.toString().trim()
                if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    Toast.makeText(this, "Please fill in your delivery details", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            placeOrder()
        }
    }

    private fun selectMethod(method: String) {
        selectedMethod = method
        val cardSelected = method == "card"

        binding.btnMethodCard.setBackgroundResource(
            if (cardSelected) R.drawable.chip_selected_bg else R.drawable.chip_unselected_bg
        )
        binding.btnMethodCard.setTextColor(
            getColor(if (cardSelected) R.color.white else R.color.navy)
        )
        binding.btnMethodCash.setBackgroundResource(
            if (!cardSelected) R.drawable.chip_selected_bg else R.drawable.chip_unselected_bg
        )
        binding.btnMethodCash.setTextColor(
            getColor(if (!cardSelected) R.color.white else R.color.navy)
        )

        binding.cardDetailsLayout.visibility =
            if (cardSelected) View.VISIBLE else View.GONE
    }

    private fun placeOrder() {
        CartManager.clear()
        val intent = Intent(this, OrderConfirmationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}