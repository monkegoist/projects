import expectj.ExpectJ
import expectj.Spawn
import org.codehaus.gmaven.mojo.GroovyMojo
import org.jfrog.maven.annomojo.annotations.*

/*
 * Uploads project build artifacts to the specified host.
 */

@MojoAggregator
@MojoGoal('upload')
@MojoRequiresDirectInvocation
@MojoRequiresOnline
class UploadMojo extends GroovyMojo {

    @MojoParameter(expression = '${system}', required = true, description = 'System to upload data to')
    public String system

    @MojoParameter(expression = '${user.name}', description = 'User login')
    public String user

    @MojoParameter(expression = '${password}', description = 'User password', defaultValue = 'N/A')
    public String password

    @MojoParameter(required = true, description = 'List of files to upload')
    public File[] files

    private String host
    private String userDir

    @Override
    void execute() {

        log.info('------------------------------------------------------------------------')
        log.info('Execution parameters:')
        log.info('  -- system   : ' + system)
        log.info('  -- user     : ' + user)
        log.info('  -- password : ' + password)

        userDir = '/home/' + user

        host = Configuration.getSystemHost(system)

        if (host == null)
            fail('Incorrect system was specified! List of available systems: ' + Configuration.getAvailableSystems())

        // Note: sleep is needed for nice output printing

        log.info('------------------------------------------------------------------------')
        log.info('(1) Uploading files to ' + system)
        log.info('------------------------------------------------------------------------')

        uploadFiles()

        Thread.sleep(500)

        log.info('------------------------------------------------------------------------')
        log.info('(2) Chmoding shell scripts')
        log.info('------------------------------------------------------------------------')

        chmod()

        Thread.sleep(500)

        log.info('------------------------------------------------------------------------')
        log.info('All done!')
    }

    private void uploadFiles() {
        def worker = new AntBuilder()
        files.each {
            if (!it.exists()) fail('Couldn\'t find file ' + it.name)
            worker.scp(
                    file: it.path,
                    todir: user + '@' + host + ':' + userDir,
                    password: password,
                    verbose: true,
                    trust: true
            )
        }
    }

    private void chmod() {
        def command = 'chmod +x '
        def filesToChmod = []
        files.each { if (it.absolutePath.endsWith('sh')) filesToChmod.add(it) }
        def args = filesToChmod.collect { ((File) it).name }.join(' ')
        try {
            ExpectJ expect = new ExpectJ(10)
            Spawn shell = expect.spawn(host, 22, user, password)
            shell.send('cd ' + userDir + '\n')
            shell.send(command + args + '\n')
            shell.send('exit\n')
            shell.expectClose()
        } catch (IOException e) {
            log.error(e)
            fail('Failed to execute chmod')
        }
    }
}