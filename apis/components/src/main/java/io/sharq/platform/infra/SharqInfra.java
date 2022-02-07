package io.sharq.platform.infra;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;
import io.sharq.platform.components.GenericComponentStatus;

@Group("platform.sharq.io")
@Version("v1alpha1")
@Kind("SharqInfra")
public class SharqInfra extends CustomResource<SharqInfraSpec, GenericComponentStatus> implements Namespaced {

    /**
     *
     */
    private static final long serialVersionUID = -6827030371261125293L;
}
