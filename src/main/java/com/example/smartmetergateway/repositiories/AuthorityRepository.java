package com.example.smartmetergateway.repositiories;

import com.example.smartmetergateway.entities.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

/*
Ermöglicht den Zugriff auf Authority Entitäten in der Datenbank.
Erweitert JpaRepository, um Standardfunktionen wie das Speichern, Löschen und Suchen von Entitäten zu ermöglichen.
Der Vorteil dieser Erweiterung ist, dass bereits viele Standardfunktionen bereitgestellt werden, die nicht explizit selbst implementiert werden müssen.
Dazu zählt beispielsweise das Suchen von Entitäten anhand von Attributen, oder die CRUD-Operationen.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}