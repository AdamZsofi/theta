plugins {
    id("kotlin-common")
}

dependencies {
    implementation(project(":theta-common"))
    implementation(project(":theta-core"))
    implementation(project(":theta-xcfa"))
    implementation(project(":theta-c-frontend"))
}
