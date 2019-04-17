import React from 'react';
import { Badge, Divider } from 'antd';
import httpFetch from 'share/httpFetch';

const edit = record => {
  window.instances.slide.show(record);
  // window.instances.form.setValues(record);
};

const remove = record => {
  alert();
};

export default {
  status: value => {
    return value ? <Badge status="success" text="启用" /> : <Badge status="error" text="禁用" />;
  },
  options: (value, record) => {
    return (
      <span>
        <a onClick={() => edit(record)}>编辑</a>
        <Divider type="vertical" />
        <a onClick={() => remove(record)}>删除</a>
      </span>
    );
  },
};
