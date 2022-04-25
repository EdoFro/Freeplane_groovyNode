package edofro.freeplane.groovynode

import org.freeplane.plugin.script.FreeplaneScriptBaseClass
import org.freeplane.plugin.script.FreeplaneScriptBaseClass.ConfigProperties


class GN {

//region: properties

    static final String attributeForExtensions =  new ConfigProperties().getProperty('groovyConsole_attributeForExtensions','file_ext')

//end:

//region: groovy Node

    public static boolean isGroovyNode(n){
        return (isExtensionNode(n, 'groovy') || n['script1']?true:false)
    }

    public static String scriptFromNode(n){
        def sc = scriptAndSourceFromNode(n)[0]
        sc = sc?.class == File? sc.text : sc
        return sc
    }
    
    // order of priority
    // groovy file   >  script1  >  ".groovy" note  >  empty
    def public static scriptAndSourceFromNode(n){
        def script
        def source
        if (isGroovyNode(n)){
            if ( extensionFromNodeFile(n) == 'groovy' ) {
                script  = n.link.file//.text
                source  = 'file'
            } else if ( n['script1']?true:false ){
                script  = n['script1'].plain.toString().trim()
                source  = 'script1'
            } else if ( n.note ){
                script  = n.note.toString()
                source  = 'note'
            }
        }
        return [script, source]
    }
    
//end:

//region: get/set/is extension from selected node

    def private static extensionFromNode(n){
        extensionFromAttribute(n)?:extensionFromDetails(n)?:extensionFromText(n)?:null
    }
    
    def private static extensionFromAttribute(n){
        n[attributeForExtensions]?:null
    }
    
    def private static extensionFromDetails(n){
        n.details?.size()>1?n.details?[0]=='.'?n.details.drop(1).takeBefore(' ').takeBefore('\n')?:n.details.drop(1).takeBefore('\n')?:n.details.drop(1).takeBefore(' ')?:n.details.drop(1):null:null
    }
    
    def private static extensionFromText(n){
        n.text.reverse().takeBefore('.').reverse()
    }

    def private static extensionFromFilePath(filepath){
        return filepath.reverse().split("\\.")[0].reverse().toLowerCase()
    }

    public static void setExtension(n, ext){
        // If it's allready defined --> do nothing
        if(extensionFromAttribute(n)==ext || extensionFromDetails(n)==ext) return
        //I prefer it in this order:
            // only details
            // if details are beeing Used --> attribute
        if(!n.details){
            n.details = '.' + ext
        } else {
            n[attributeForExtensions] = ext
        }
    }

    def private static extensionFromNodeFile(n){
        (n.link && n.link.uri && n.link.uri.scheme == 'file')?extensionFromFilePath(n.link.uri.path):null
    }

    def private static isExtensionNode(n, extension){
        def ext = extensionFromNodeFile(n)?:extensionFromNode(n)
        return ext?ext==extension:false
    }

//end:


}
