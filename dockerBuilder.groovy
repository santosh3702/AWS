import java.lang.System
import java.io.File
import hudson.model.*
import jenkins.model.*
import org.jenkinsci.plugins.dockerbuildstep.*
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest

def instance = Jenkins.getInstance()
  
def docker = instance.getDescriptor(DockerBuilder.class)

JSONObject myString = new JSONObject()

myString.put("dockerUrl", "unix:///var/run/docker.sock")

myString.put("dockerVersion", "")
myString.put("dockerCertPath", "")


docker.configure(null,myString)

instance.save()
