package io.sharq.platform.components;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("platform.sharq.io")
@Version("v1alpha1")
@Kind("OutboundConnection")
//@Crd(group = "platform.sharq.io", version = "v1alpha1", status = KeyValueStoreStatus.class)
public class OutboundConnection extends CustomResource<GenericComponentSpec, GenericComponentStatus> implements Namespaced {

    /**
     *
     */
    private static final long serialVersionUID = -1673468590782797053L;

}
