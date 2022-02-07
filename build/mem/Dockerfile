# FROM quay.io/famargon/sharq-platform-component-mem:latest as sharq

# # COPY --chown=1001:root entrypoint.sh /sharq-entrypoint.sh

# RUN ls /home

# FROM registry.access.redhat.com/ubi8/ubi-minimal:8.3

# COPY --from=sharq /application /sharq/application
# # ADD entrypoint.sh /sharq/entrypoint.sh
# COPY --chown=1001:root entrypoint.sh /sharq/entrypoint.sh

# WORKDIR /sharq

# ENTRYPOINT [ "./sharq/entrypoint.sh" ]

FROM registry.access.redhat.com/ubi8/ubi-minimal:8.3
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work
COPY --chown=1001:root components/mem/target/*-runner /work/application

COPY --chown=1001:root build/mem/entrypoint.sh /work/sharq-entrypoint.sh

USER 1001

ENTRYPOINT [ "./sharq-entrypoint.sh" ]

# CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]