package com.gbsoft.smartpatient.ui.main.chatlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.data.ChatIdentifier;
import com.gbsoft.smartpatient.data.HealthPersonnel;
import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.databinding.FragmentChatListBinding;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.gbsoft.smartpatient.ui.main.MainViewModel;
import com.gbsoft.smartpatient.ui.main.appointmentlist.HealthPersonnelListAdapter;
import com.gbsoft.smartpatient.ui.main.appointmentlist.PatientListAdapter;
import com.gbsoft.smartpatient.ui.main.appointmentlist.PatientVM;
import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment representing a list of chats of the logged in user.
 */
@AndroidEntryPoint
public class ChatListFrag extends Fragment {
    private FragmentChatListBinding binding;
    private ChatListVM viewModel;

    private RVEmptyObserver usrObv;
    private RVEmptyObserver chatIdObv;

    private PatientListAdapter pAdapter;
    private HealthPersonnelListAdapter hpAdapter;
    private ChatListAdapter clAdapter;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.chatListToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), Navigation.findNavController(view),
                ((MainActivity) requireActivity()).getAppBarConfig());

        viewModel = new ViewModelProvider(this).get(ChatListVM.class);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        new ViewModelProvider(requireActivity()).get(MainViewModel.class).
                isCurrentUserAPatient.observe(getViewLifecycleOwner(), isPatient -> {
            if (isPatient == null) return;
            viewModel.setIsPatient(isPatient);
            setAvailableUserRecyclerView(!isPatient);
            setChatIdListRecyclerView(!isPatient);
        });
    }

    private void setAvailableUserRecyclerView(boolean isHp) {
        usrObv = new RVEmptyObserver(binding.rvAvailableUsrList, binding.tvNoAvailableUsr);
        if (isHp) {
            FirestoreRecyclerOptions<Patient> usrOptions = new FirestoreRecyclerOptions.Builder<Patient>()
                    .setQuery(viewModel.getAllPatientsQuery(), Patient.class)
                    .setLifecycleOwner(getViewLifecycleOwner())
                    .build();

            pAdapter = new PatientListAdapter(usrOptions, usrObv, PatientVM.ON_CLICK_TO_CHAT_DETAILS);
            pAdapter.registerAdapterDataObserver(usrObv);
            binding.rvAvailableUsrList.setAdapter(pAdapter);
        } else {
            FirestoreRecyclerOptions<HealthPersonnel> usrOptions = new FirestoreRecyclerOptions.Builder<HealthPersonnel>()
                    .setQuery(viewModel.getHealthPersonnelQuery(), HealthPersonnel.class)
                    .setLifecycleOwner(getViewLifecycleOwner())
                    .build();

            hpAdapter = new HealthPersonnelListAdapter(usrOptions, usrObv, PatientVM.ON_CLICK_TO_CHAT_DETAILS);
            hpAdapter.registerAdapterDataObserver(usrObv);
            binding.rvAvailableUsrList.setAdapter(hpAdapter);
        }
        binding.rvAvailableUsrList.setHasFixedSize(false);
    }

    private void setChatIdListRecyclerView(boolean isHp) {
        FirestoreRecyclerOptions<ChatIdentifier> chatIdOptions = new FirestoreRecyclerOptions.Builder<ChatIdentifier>()
                .setQuery(viewModel.getChatIdQuery(isHp), ChatIdentifier.class)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        chatIdObv = new RVEmptyObserver(binding.rvChatList, binding.tvNoChatId);
        clAdapter = new ChatListAdapter(chatIdOptions, chatIdObv, isHp);
        clAdapter.registerAdapterDataObserver(chatIdObv);
        binding.rvChatList.setHasFixedSize(true);
        binding.rvChatList.setAdapter(clAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel = null;
    }
}