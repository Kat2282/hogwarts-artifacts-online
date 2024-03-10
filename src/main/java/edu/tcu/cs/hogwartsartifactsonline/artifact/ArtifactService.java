package edu.tcu.cs.hogwartsartifactsonline.artifact;

import edu.tcu.cs.hogwartsartifactsonline.artifact.utils.IdWorker;
import jakarta.transaction.Transactional;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
    //puts every method in this class in its own transaction.
    // if a method throws an exception while executing modification to a database, that method will be abandoned (a rowback occurs)
public class ArtifactService {

    //inject dependencies into dependent
    //artifactService depends on artifactRepository, so well inject repository into service
    private final ArtifactRepository artifactRepository;

    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId) {
        return this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
    }

    public List<Artifact> findAll() {
        return this.artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact) {
        newArtifact.setId(idWorker.nextId() + "");
        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact update) {
        return this.artifactRepository.findById(artifactId)
                .map(oldArtifact -> {
                    oldArtifact.setName(update.getName());
                    oldArtifact.setDescription(update.getDescription());
                    oldArtifact.setImageUrl(update.getImageUrl());
                    return this.artifactRepository.save(oldArtifact);
                })
                //this line was different on his code, wasn't working, checked the docs and it was different so I used it instead
                .orElseThrow(() -> new ObjectNotFoundException(Optional.of("artifact"), artifactId));
    }

    public void delete(String artifactId) {
        //find if id exists or not
        this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
        this.artifactRepository.deleteById(artifactId);
    }
}

