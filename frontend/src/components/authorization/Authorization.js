import React, {useEffect} from 'react';
import {redirect, useNavigate, useSearchParams} from "react-router-dom";
import {authorizeByVk} from "../../requests";
import Cookie from "js-cookie";
import {useDispatch} from "react-redux";

const Authorization = () => {

    const [params] = useSearchParams()
    const navigate = useNavigate();
    const dispatch = useDispatch();
    useEffect(() => {
        const code = params.get('code');
        authorizeByVk(code).then(r => {
            Cookie.set("jwt", r.data.access_token);
            dispatch({type: "SET_IS_AUTH", isAuth: true})
            navigate('/')
        });

    }, []);

    return (
        <div>
            Авторизация...
        </div>
    );
};

export default Authorization;