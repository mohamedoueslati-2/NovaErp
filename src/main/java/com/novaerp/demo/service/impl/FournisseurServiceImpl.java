package com.novaerp.demo.service.impl;

import com.novaerp.demo.model.entity.Fournisseur;
import com.novaerp.demo.model.entity.User;
import com.novaerp.demo.repository.FournisseurRepository;
import com.novaerp.demo.repository.UserRepository;
import com.novaerp.demo.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FournisseurServiceImpl implements FournisseurService {

    private final FournisseurRepository fournisseurRepository;
    private final UserRepository userRepository;

    @Override
    public List<Fournisseur> findAll() {
        return fournisseurRepository.findAll();
    }

    @Override
    public Fournisseur findById(Long id) {
        return fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'ID : " + id));
    }

    @Override
    @Transactional
    public Fournisseur save(Fournisseur fournisseur, String currentUserEmail) {
        if (fournisseur.getId() != null) {
            // 🔹 Cas édition
            Fournisseur existing = fournisseurRepository.findById(fournisseur.getId())
                    .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));

            existing.setCode(fournisseur.getCode());
            existing.setNom(fournisseur.getNom());
            existing.setEmail(fournisseur.getEmail());
            existing.setTelephone(fournisseur.getTelephone());
            existing.setAdresse(fournisseur.getAdresse());

            // tu ne touches pas à createdBy (il reste celui d’origine)
            return fournisseurRepository.save(existing);
        } else {
            // 🔹 Cas création
            User createdBy = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            fournisseur.setCreatedBy(createdBy);
            return fournisseurRepository.save(fournisseur);
        }
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!fournisseurRepository.existsById(id)) {
            throw new RuntimeException("Fournisseur non trouvé avec l'ID : " + id);
        }
        fournisseurRepository.deleteById(id);
    }
}