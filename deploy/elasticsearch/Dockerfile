FROM docker.elastic.co/elasticsearch/elasticsearch:7.17.5
ARG IK_VERSION=7.17.5
ARG IK_PATH=/usr/share/elasticsearch/plugins/analysis-ik

# 添加IK分词器并配置
RUN mkdir -p ${IK_PATH}
COPY elasticsearch/plugin/elasticsearch-analysis-ik-7.17.5.zip ${IK_PATH}
# ADD https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-${IK_VERSION}.zip ${IK_PATH}
RUN unzip ${IK_PATH}/elasticsearch-analysis-ik-${IK_VERSION}.zip -d ${IK_PATH}
RUN rm ${IK_PATH}/elasticsearch-analysis-ik-${IK_VERSION}.zip
COPY elasticsearch/config/ik/* ${IK_PATH}/config/

EXPOSE 9200 9300

CMD ["elasticsearch"]