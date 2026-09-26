FROM python:3.10-slim
LABEL maintainer="jeep.jipu@gmail.com" version="1.0"
WORKDIR /root/beancount

RUN pip install beancount fava

EXPOSE 5001
CMD ["fava", "--host", "0.0.0.0", "--port", "5001", "moneybook.bean"]
