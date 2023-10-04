package com.example.rickandmorty.views.characters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.databinding.CharacterDetailsBinding
import com.example.rickandmorty.models.Result

class CharacterAdapter(private val listener: CharacterSelected) :
    RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder>() {
    private val characters = mutableListOf<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CharacterDetailsBinding.inflate(layoutInflater, parent, false)
        return CharacterViewHolder(binding)
    }

    override fun getItemCount() = characters.size

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.bind(characters[position], listener)
    }

    fun updateCharacters(newCharacters: List<Result>) {
        val diffUtil = CharacterDiffUtil(characters, newCharacters)
        val result = DiffUtil.calculateDiff(diffUtil)
        characters.clear()
        characters.addAll(newCharacters)
        result.dispatchUpdatesTo(this)
    }

    inner class CharacterViewHolder(private val binding: CharacterDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Result, listener: CharacterSelected) {
            binding.characterName.text = character.name
            binding.characterSpecie.text = character.species
            binding.characterStatus.text = character.status
            binding.root.setOnClickListener {
                listener.showCharacterLocation(character)
            }
        }
    }

    inner class CharacterDiffUtil(
        private val oldList: List<Result>,
        private val newList: List<Result>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
                    && oldList[oldItemPosition].gender == newList[newItemPosition].gender
                    && oldList[oldItemPosition].name == newList[newItemPosition].name
                    && oldList[oldItemPosition].species == newList[newItemPosition].species
                    && oldList[oldItemPosition].created == newList[newItemPosition].created
                    && oldList[oldItemPosition].episode == newList[newItemPosition].episode
        }
    }
}

interface CharacterSelected {
    fun showCharacterLocation(character: Result)
}