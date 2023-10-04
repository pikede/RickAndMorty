package com.example.rickandmorty.views.characters

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rickandmorty.databinding.FragmentRickAndMortyCharacterBinding
import com.example.rickandmorty.models.Result
import com.example.rickandmorty.views.location.CharacterLocationFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RickAndMortyCharacterFragment : Fragment(), CharacterSelected {
    private var _binding: FragmentRickAndMortyCharacterBinding? = null
    private val binding: FragmentRickAndMortyCharacterBinding get() = _binding!!
    private lateinit var characterAdapter: CharacterAdapter
    private val viewModel: CharacterViewModel by sharedViewModel()

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
        setupObservers()
        setupUI()
    }

    private fun setupObservers() {
        with(viewModel) {
            rickAndMortyCharacters.observe(viewLifecycleOwner) { updateCharacters(it) }
            errorMessage.observe(viewLifecycleOwner) { showError(it) }
            getCharacters()
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
}