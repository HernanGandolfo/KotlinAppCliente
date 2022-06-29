package com.example.appclient.track

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.appclient.Constants
import com.example.appclient.R
import com.example.appclient.databinding.FragmentTrackBinding
import com.example.appclient.entities.Order
import com.example.appclient.order.OrderAux
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class TrackFragment : Fragment() {
    private var binding: FragmentTrackBinding? = null

    private var order: Order? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackBinding.inflate(inflater, container, false)
        binding?.let {
            return  it.root
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getOrder()
    }

    //Seguimiento de orden
    private fun getOrder() {
        order = (activity as? OrderAux)?.getOrderSelected()

        order?.let {
            updateUI(it)

            getOrderInRealtime(it.id)

            setupActionBar()

        }
    }

    //Update en proceso de orden
    private fun updateUI(order: Order) {
        binding?.let {
            it.progressBar.progress = order.status * (100/3) - 15

            it.cbOrdered.isChecked = order.status > 0
            it.cbPreparing.isChecked = order.status > 1
            it.cbSent.isChecked = order.status > 2
            it.cbDelivered.isChecked = order.status > 3
        }
    }

    // Consulta sobre el seguimiento tiempo real
    private fun getOrderInRealtime(orderId: String){
        val db = FirebaseFirestore.getInstance()

        val orderRef = db.collection(Constants.COLL_REQUESTS).document(orderId)
        orderRef.addSnapshotListener { snapshot, error ->
            if (error != null){
                Toast.makeText(activity, "Error al consultar esta orden.", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()){
                val order = snapshot.toObject(Order::class.java)
                order?.let {
                    it.id = snapshot.id

                    updateUI(it)
                }
            }
        }
    }

    //Manipulacion del Estado en Activity
    private fun setupActionBar(){
        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            it.supportActionBar?.title = getString(R.string.track_title)
            setHasOptionsMenu(true)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    //Destruyo la modificacion
    override fun onDestroy() {
        (activity as? AppCompatActivity)?.let {
            it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            it.supportActionBar?.title = getString(R.string.order_title)
            setHasOptionsMenu(false)
        }
        super.onDestroy()
    }
}