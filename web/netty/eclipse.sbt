import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

EclipseKeys.eclipseOutput := Some(".target")

EclipseKeys.withSource := true
