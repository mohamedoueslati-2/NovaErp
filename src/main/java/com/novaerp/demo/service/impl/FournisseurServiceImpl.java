package com.novaerp.demo.service.imp;

import com.novaerp.demo.model.entity.Fournisseur;
import com.novaerp.demo.repository.FournisseurRepository;
import com.novaerp.demo.service.FournisseurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FournisseurServiceImpl implements FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    @Override
    public Fournisseur create(Fournisseur fournisseur) {
        if (fournisseurRepository.existsByCode(fournisseur.getCode())) {
            throw new RuntimeException("Un fournisseur avec ce code existe déjà");
        }
        if (fournisseurRepository.existsByEmail(fournisseur.getEmail())) {
            throw new RuntimeException("Un fournisseur avec cet email existe déjà");
        }
        return fournisseurRepository.save(fournisseur);
    }

    @Override
    public Fournisseur update(Long id, Fournisseur fournisseur) {
        Fournisseur existing = findById(id);
        existing.setCode(fournisseur.getCode());
        existing.setNom(fournisseur.getNom());
        existing.setAdresse(fournisseur.getAdresse());
        existing.setTelephone(fournisseur.getTelephone());
        existing.setEmail(fournisseur.getEmail());
        return fournisseurRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Fournisseur findById(Long id) {
        return fournisseurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé avec l'id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fournisseur> findAll() {
        return fournisseurRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        Fournisseur fournisseur = findById(id);
        fournisseurRepository.delete(fournisseur);
    }
}