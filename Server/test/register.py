# !/usr/bin/env python
# -*- coding: utf-8 -*-
import sys
reload(sys)
sys.setdefaultencoding("utf-8")
import json
import requests
import os
import torndb
import time
data = {}
data['user_name'] = "我的"
data['password'] = '123456'
data['config'] = '我的中文'
urlr = "http://127.0.0.1:9001/register"
r = requests.post(urlr, data = data)
print r.text
