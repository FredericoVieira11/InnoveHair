package com.example.innovehair.view.collaboratorDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.innovehair.R
import com.example.innovehair.databinding.FragmentCollaboratorDetailsBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CollaboratorDetailsFragment : Fragment() {

    private lateinit var binding: FragmentCollaboratorDetailsBinding
    private val viewModel by viewModels<CollaboratorDetailsViewModel>()
    private val args: CollaboratorDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollaboratorDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setIcon(R.drawable.ic_arrow_left_yellow)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        lifecycleScope.launch {
            viewModel.getServiceHistoryByCollaboratorId(args.collaboratorData?.id)
        }

        viewModel.dates.observe(viewLifecycleOwner) { (scheduleServiceSize, scheduleApplicationSize) ->
            binding.servicesNumber.text = scheduleServiceSize?.toString() ?: "- - -"
            binding.applicationsNumber.text = scheduleApplicationSize?.toString() ?: "- - -"
        }

        binding.cvDeleteCollaborator.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteAllCollaboratorData(args.collaboratorData?.id ?: "")
            }
        }

        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            binding.loadingGroup.isVisible = viewState is CollaboratorDetailsViewState.Loading
            if (viewState is CollaboratorDetailsViewState.Success) {
                findNavController().popBackStack()
            }
        }

        Glide.with(this)
            .load(args.collaboratorData?.uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(R.drawable.ic_profile_default)
            .placeholder(R.drawable.ic_profile_default)
            .skipMemoryCache(true)
            .into(binding.userImgProfile)

        binding.userName.text = args.collaboratorData?.user?.name ?: ""
        binding.userPhoneNumber.text = args.collaboratorData?.user?.phoneNumber ?: ""
        binding.userEmail.text = args.collaboratorData?.user?.email ?: ""
        val currentDateTime = args.collaboratorData?.user?.creationDate ?: ""
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDateTime)
        binding.userCreatedDate.text = formattedDate
    }
}