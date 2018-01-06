// Based on ideas presented in Josh Suereth's book sbt in Action
// also cf. https://github.com/mariussoutier/sbt-unpack
import sbt._
import Keys._

val dependentJarDirectory = settingKey[File]("location of the unpacked dependent jars")

dependentJarDirectory := file(".") / "tmp"

val createDependentJarDirectory = taskKey[File]("create the dependent-jars directory")

createDependentJarDirectory := {
  sbt.IO.createDirectory(dependentJarDirectory.value)
  dependentJarDirectory.value
}

val excludes = List("meta-inf", "license", "play.plugins", "reference.conf")

def unpackFilter(target: File): NameFilter = (name: String) => {
  !excludes.exists(x => name.toLowerCase().startsWith(x)) &&
    !file(target.getAbsolutePath + "/" + name).exists
}

def unpack(target: File, f: File, log: Logger): Unit = {
  val artifactid = Option(f.getParentFile) map (_.getParentFile)
  val srcs = artifactid map (_ / "srcs")
  if (srcs exists (_.exists)) {
    val sjar = srcs map (_ / f.getName.replaceAll("(\\.[^\\.]*$)", "-sources.jar"))
    if (sjar exists (_.exists)) {
      log.info(s"Unpacking ${sjar.get}")
      sbt.IO.unzip(sjar.get, target, unpackFilter(target))
    } else log.info(s"No sources available for ${sjar.get}")
  } else log.info(s"No sources available for ${artifactid.get}")
}

val unpackJars = taskKey[Seq[_]]("unpacks dependent JARs into target/dependent-jars")

// noinspection UnitInMap
unpackJars := {
  val dir = createDependentJarDirectory.value
  val log = streams.value.log
  val dep = (dependencyClasspath in Runtime).value.files
  dep map (f => unpack(dir, f, log))
}
