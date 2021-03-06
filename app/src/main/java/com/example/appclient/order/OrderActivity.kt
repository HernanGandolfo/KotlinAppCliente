package com.example.appclient.order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appclient.Constants
import com.example.appclient.R
import com.example.appclient.chat.ChatFragment
import com.example.appclient.databinding.ActivityOrderBinding
import com.example.appclient.entities.Order
import com.example.appclient.track.TrackFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class OrderActivity : AppCompatActivity() , OnOrderListener, OrderAux{

    private lateinit var binding: ActivityOrderBinding

    private lateinit var adapter: OrderAdaper

    private lateinit var orderSelected: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFirestore()

    }

    private fun setupRecyclerView() {
        adapter = OrderAdaper(mutableListOf(), this)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderActivity)
            adapter = this@OrderActivity.adapter
        }
    }

    private fun setupFirestore(){
        FirebaseAuth.getInstance().currentUser?.let {  user ->
            val db = FirebaseFirestore.getInstance()

            db.collection(Constants.COLL_REQUESTS)
                //.orderBy(Constants.PROP_DATE, Query.Direction.ASCENDING)
                //.whereEqualTo(Constants.PROP_CLIENT_ID, user.uid)
                //.whereIn(Constants.PROP_STATUS, listOf(1, 4))
                //.whereNotIn(Constants.PROP_STATUS, listOf(4))
                //.whereGreaterThan(Constants.PROP_STATUS, 2)
                //.whereLessThan(Constants.PROP_STATUS, 4)
                //.whereEqualTo(Constants.PROP_STATUS, 3)
                //.whereGreaterThanOrEqualTo(Constants.PROP_STATUS, 2)
                /*.whereEqualTo(Constants.PROP_CLIENT_ID, user.uid)
                .orderBy(Constants.PROP_STATUS, Query.Direction.DESCENDING)*/
                .whereEqualTo(Constants.PROP_CLIENT_ID, user.uid)
                /*.orderBy(Constants.PROP_STATUS, Query.Direction.ASCENDING)
                .whereLessThan(Constants.PROP_STATUS, 4)*/
                //.orderBy(Constants.PROP_DATE, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    for (document in it){
                        val order = document.toObject(Order::class.java)
                        order.id = document.id
                        adapter.add(order)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al consultar los datos.", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    override fun onTrack(order: Order) {
        orderSelected = order

        val fragment = TrackFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.containerMain, fragment)
            .addToBackStack(null)
            .commit()
    }

    //Comienza el Evento de Chat
    override fun onStartChat(order: Order) {
      orderSelected = order

        val fragment = ChatFragment()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.containerMain, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun getOrderSelected(): Order = orderSelected
}