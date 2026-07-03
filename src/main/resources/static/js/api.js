const API="http://localhost:8080/api";

function token(){
    return localStorage.getItem("token");
}

async function request(url,method="GET",body=null){

    const options={
        method,
        headers:{
            "Content-Type":"application/json"
        }
    };

    if(token()){
        options.headers.Authorization="Bearer "+token();
    }

    if(body){
        options.body=JSON.stringify(body);
    }

    const response=await fetch(API+url,options);

    const text=await response.text();

    try{
        return JSON.parse(text);
    }catch{
        return text;
    }
}