# !/usr/bin/env python
# -*- coding: utf-8 -*-

from __future__ import absolute_import

from tornado.log import LogFormatter as TornadoLogFormatter
import logging
import logging.handlers
import logging.config
import os.path
import types

from logconfig import dictconfig


def initialize_logging():
    cfg = {
        'version': 1,
        'disable_existing_loggers': False,
        'formatters': {
            'verbose': {
                'format': "[%(asctime)s] %(levelname)s [%(name)s:%(lineno)s] %(message)s",
                'datefmt': "%Y-%m-%d %H:%M:%S"
            },
            'simple': {
                'format': '%(levelname)s %(message)s'
            },
        },
        'handlers': {
            'console': {
                'level': 'DEBUG',
                'class': 'logging.StreamHandler',
                'formatter': 'verbose',
                'stream': 'ext://sys.stdout'
            },
            'file': {
                'level': 'DEBUG',
                'class': 'logging.handlers.RotatingFileHandler',
                # 当达到10MB时分割日志
                'maxBytes': 1024 * 1024 * 10,
                # 最多保留50份文件
                'backupCount': 50,
                # If delay is true,
                # then file opening is deferred until the first call to emit().
                'delay': True,
                'filename': 'matrix-file-system.log',
                'formatter': 'verbose'
            }
        },
        'loggers': {
            '': {
                'handlers': ['console'],
                'level': 'INFO',
            },
        }
    }
    # logging.config.dictConfig(cfg)
    dictconfig.dictConfig(cfg)
