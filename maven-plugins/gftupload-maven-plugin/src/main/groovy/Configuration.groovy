class Configuration {

    def static final SYSTEMS = [
            'gftdev0'   : 'gftdev0.in.devexperts.com',
            'gftdev2'   : 'gftdev2.in.devexperts.com',
            'gftcore'   : 'gftcore.in.devexperts.com',
            'gftcore2'  : 'gftcore2.in.devexperts.com',
            'gftbindev' : 'gftbindev.in.devexperts.com'
    ]

    static String getSystemHost(String name) {
        return SYSTEMS.containsKey(name) ? SYSTEMS.get(name) : null
    }

    static String getAvailableSystems() {
        return SYSTEMS.collect { it.key }.join(', ')
    }

}