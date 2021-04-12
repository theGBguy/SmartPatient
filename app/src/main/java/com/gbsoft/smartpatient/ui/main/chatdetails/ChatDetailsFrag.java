package com.gbsoft.smartpatient.ui.main.chatdetails;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gbsoft.smartpatient.data.Message;
import com.gbsoft.smartpatient.data.User;
import com.gbsoft.smartpatient.databinding.FragmentChatDetailsListBinding;
import com.gbsoft.smartpatient.ui.RVEmptyObserver;
import com.gbsoft.smartpatient.ui.main.MainActivity;
import com.gbsoft.smartpatient.ui.main.MainViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment containing the chats between a patient and a health personnel.
 */

@AndroidEntryPoint
public class ChatDetailsFrag extends Fragment {
    public static final String KEY_RECEIVER_INFO = "receiver_info";

    private FragmentChatDetailsListBinding binding;
    private ChatDetailsVM vm;

    private ChatDetailsListAdapter adapter;
    private RVEmptyObserver msgObv;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatDetailsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WeakReference<MaterialToolbar> toolbar = new WeakReference<>(binding.chatDetailsToolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar.get());
        NavigationUI.setupWithNavController(toolbar.get(), Navigation.findNavController(view),
                ((MainActivity) requireActivity()).getAppBarConfig());

        vm = new ViewModelProvider(this).get(ChatDetailsVM.class);
        User current = new ViewModelProvider(requireActivity()).get(MainViewModel.class).getCurrentUser();
        vm.init(requireArguments(), current);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(vm);

        msgObv = new RVEmptyObserver(binding.rvChatDetails, binding.tvNoMsg);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(vm.getMessagesQuery(), Message.class)
                .setLifecycleOwner(getViewLifecycleOwner())
                .build();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.rvChatDetails.setLayoutManager(layoutManager);
        binding.rvChatDetails.setHasFixedSize(true);

        adapter = new ChatDetailsListAdapter(options, msgObv, current.getId() + "_" + current.getName());
        adapter.registerAdapterDataObserver(msgObv);
        binding.rvChatDetails.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.unregisterAdapterDataObserver(msgObv);
        }
        binding = null;
        vm = null;
    }
}