package com.example.rickandmorty.views.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmorty.databinding.FragmentRickAndMortyCharacterBinding
import com.example.rickandmorty.models.Result
import com.example.rickandmorty.views.location.CharacterLocationFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RickAndMortyCharacterFragment : Fragment(), CharacterSelected, OnQueryTextListener {
    private var _binding: FragmentRickAndMortyCharacterBinding? = null
    private val binding: FragmentRickAndMortyCharacterBinding get() = _binding!!
    private lateinit var characterAdapter: CharacterAdapter
    private val viewModel: CharacterViewModel by viewModel()

    companion object {
        fun newInstance() = RickAndMortyCharacterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRickAndMortyCharacterBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupObservers() {
        with(viewModel) {
            rickAndMortyCharacters.observe(viewLifecycleOwner) { updateCharacters(it) }
            errorMessage.observe(viewLifecycleOwner) { showError(it) }
        }
    }

    private fun updateCharacters(characters: List<Result>) {
        binding.loadingProgress.isVisible = true
        characterAdapter.updateCharacters(characters)
        binding.loadingProgress.isVisible = false
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(
            requireContext(),
            errorMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setupUI() {
        binding.characters.run {
            layoutManager = LinearLayoutManager(requireContext())
            characterAdapter = CharacterAdapter(this@RickAndMortyCharacterFragment)
            adapter = characterAdapter
            addOnScrollListener(onScrollListener)
        }
        binding.characterSearch.setOnQueryTextListener(this)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                viewModel.getCharacters()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showCharacterLocation(character: Result) {
        activity?.supportFragmentManager?.beginTransaction()?.replace(
            android.R.id.content, CharacterLocationFragment.newInstance(
                bundleOf(
                    CharacterLocationFragment.CharacterImage to character.image,
                    CharacterLocationFragment.CharacterID to character.id
                )
            )
        )?.addToBackStack(null)?.commit()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            viewModel.getCharacters(query)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        // this loads more characters when the text field gets empty
        if(newText.isNullOrEmpty() || newText.isNullOrBlank()){
            viewModel.getCharacters("")
        }
        return false
    }
}