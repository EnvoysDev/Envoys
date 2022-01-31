package com.perkelle.dev.envoys.holograms

class MissingDependencyException(dependency: String): Exception("Missing dependency: $dependency")