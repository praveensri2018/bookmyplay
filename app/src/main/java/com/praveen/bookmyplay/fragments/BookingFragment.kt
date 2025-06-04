package com.praveen.bookmyplay.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.praveen.bookmyplay.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

data class Game(
    val name: String,
    val price: Int
)
/**
 * A simple [Fragment] subclass.
 * Use the [BookingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewBooking)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Dummy data
        val gameList = listOf(
            Game("FIFA 23", 150),
            Game("Mortal Kombat", 200),
            Game("Call of Duty", 250),
            Game("Tekken 7", 180),
            Game("PUBG", 100)
        )

        recyclerView.adapter = GameAdapter(gameList)
        return view
    }

    class GameAdapter(private val gameList: List<Game>) :
        RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

        inner class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvGameName)
            val tvPrice: TextView = view.findViewById(R.id.tvGamePrice)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_game_booking, parent, false)
            return GameViewHolder(view)
        }

        override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
            val game = gameList[position]
            holder.tvName.text = game.name
            holder.tvPrice.text = "₹ ${game.price}"
        }

        override fun getItemCount(): Int = gameList.size
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}