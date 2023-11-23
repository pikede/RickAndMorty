package com.example.rickandmorty.views.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rickandmorty.R
import com.example.rickandmorty.databinding.FragmentCharacterLocationBinding
import com.example.rickandmorty.models.Locations
import com.example.rickandmorty.views.hideView
import com.example.rickandmorty.views.showView
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel

class CharacterLocationFragment : Fragment() {
    private var _binding: FragmentCharacterLocationBinding? = null
    private val binding: FragmentCharacterLocationBinding get() = _binding!!
    private val viewModel: LocationsViewModel by viewModel()

    companion object {
        const val CharacterID = "CharacterID"
        const val CharacterImage = "CharacterImage"
        fun newInstance(bundle: Bundle) =
            CharacterLocationFragment().apply {
                arguments = bundle
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCharacterLocationBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        getLocations()
    }

    private fun getLocations() {
        arguments?.getInt(CharacterID)?.let {
            viewModel.getLocations(it)
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            characterLocation.observe(viewLifecycleOwner) { showLocations(it) }
            errorMessage.observe(viewLifecycleOwner) { showError(it) }
        }
    }

    private fun showLocations(locations: Locations?) {
        showProgressBar()
        showCharacterImage()
        showLocationInformation(locations)
        hideProgressBar()
    }

    private fun showProgressBar() {
        binding.locationsProgress.showView()
    }

    private fun showCharacterImage() {
        arguments?.getString(CharacterImage)?.let {
            Picasso.get().load(it).fit().into(binding.characterImage)
        }
    }

    private fun showLocationInformation(locations: Locations?) {
        locations?.let {
            with(binding) {
                locationName.text = it.name
                type.text = it.type
                dimension.text = it.dimension
                numberOfResidents.text = it.residents?.size.toString()
            }
            it.name
        } ?: showError(getString(R.string.no_locations_available))
    }

    private fun hideProgressBar() {
        binding.locationsProgress.hideView()
    }

    private fun showError(errorMessage: String) {
        Toast.makeText(
            requireContext(),
            errorMessage,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}